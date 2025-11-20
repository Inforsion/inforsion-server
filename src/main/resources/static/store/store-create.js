(function () {
    const TOKEN_STORAGE_KEY = "inforsion-store-debug-token";

    const els = {
        token: document.getElementById("token-input"),
        persistToken: document.getElementById("persist-token"),
        clearToken: document.getElementById("clear-token"),
        searchForm: document.getElementById("address-search-form"),
        addressQuery: document.getElementById("address-query"),
        pageSize: document.getElementById("page-size"),
        addressMeta: document.getElementById("address-meta"),
        addressResults: document.getElementById("address-results"),
        storeForm: document.getElementById("store-form"),
        storeName: document.getElementById("store-name"),
        storeLocation: document.getElementById("store-location"),
        clearLocation: document.getElementById("clear-location"),
        locationHelper: document.getElementById("location-helper"),
        storeDescription: document.getElementById("store-description"),
        feedback: document.getElementById("feedback"),
        responseOutput: document.getElementById("response-output")
    };

    const state = {
        lastAddresses: [],
        selectedAddressIndex: null
    };

    initTokenPersistence();
    wireEvents();

    function initTokenPersistence() {
        if (!els.token || !els.persistToken) {
            return;
        }
        const saved = localStorage.getItem(TOKEN_STORAGE_KEY);
        if (saved) {
            els.token.value = saved;
            els.persistToken.checked = true;
        }

        els.token.addEventListener("input", () => {
            if (els.persistToken.checked) {
                localStorage.setItem(TOKEN_STORAGE_KEY, els.token.value);
            }
        });

        els.persistToken.addEventListener("change", () => {
            if (els.persistToken.checked) {
                localStorage.setItem(TOKEN_STORAGE_KEY, els.token.value);
            } else {
                localStorage.removeItem(TOKEN_STORAGE_KEY);
            }
        });

        if (els.clearToken) {
            els.clearToken.addEventListener("click", () => {
                els.token.value = "";
                els.persistToken.checked = false;
                localStorage.removeItem(TOKEN_STORAGE_KEY);
            });
        }
    }

    function wireEvents() {
        els.searchForm?.addEventListener("submit", handleAddressSearch);
        els.addressResults?.addEventListener("click", handleAddressPick);
        els.clearLocation?.addEventListener("click", () => {
            if (!els.storeLocation) return;
            els.storeLocation.value = "";
            els.storeLocation.dataset.latitude = "";
            els.storeLocation.dataset.longitude = "";
            state.selectedAddressIndex = null;
            updateAddressHighlight();
            updateLocationHelper();
        });
        els.storeForm?.addEventListener("submit", handleStoreSubmit);
    }

    function getNormalizedToken() {
        const raw = els.token?.value.trim();
        if (!raw) {
            throw new Error("JWT 토큰을 입력하세요.");
        }
        return raw.toLowerCase().startsWith("bearer ") ? raw : `Bearer ${raw}`;
    }

    async function handleAddressSearch(event) {
        event.preventDefault();
        if (!els.addressQuery) return;

        const query = els.addressQuery.value.trim();
        if (!query) {
            setFeedback("검색어를 입력하세요.", "error");
            return;
        }

        setFeedback("카카오 주소 검색 중...", "info");
        renderAddressResults([]);
        state.selectedAddressIndex = null;
        updateAddressHighlight();

        try {
            const size = Number(els.pageSize?.value) || 5;
            const response = await fetch(`/api/v1/stores/address-search?query=${encodeURIComponent(query)}&page=1&size=${size}`, {
                headers: {
                    "Authorization": getNormalizedToken(),
                    "Accept": "application/json"
                }
            });

            if (!response.ok) {
                const message = await extractError(response);
                throw new Error(message);
            }

            const payload = await response.json();
            state.lastAddresses = payload.addresses || [];
            renderAddressResults(state.lastAddresses);
            updateAddressMeta(payload);

            setFeedback(`${state.lastAddresses.length}건의 주소를 불러왔습니다.`, "success");
        } catch (error) {
            setFeedback(error.message || "주소 검색에 실패했습니다.", "error");
        }
    }

    function renderAddressResults(addresses) {
        if (!els.addressResults) return;
        els.addressResults.innerHTML = "";

        if (!addresses.length) {
            const empty = document.createElement("li");
            empty.className = "address-result address-result--empty";
            empty.textContent = "검색 결과가 없습니다. 다른 검색어를 시도해 보세요.";
            els.addressResults.appendChild(empty);
            return;
        }

        addresses.forEach((address, index) => {
            const li = document.createElement("li");
            li.className = "address-result";
            li.dataset.index = String(index);

            const title = document.createElement("div");
            title.className = "address-result__title";
            title.textContent = address.roadAddressName || address.addressName || "(주소 정보 없음)";
            li.appendChild(title);

            if (address.jibunAddressName) {
                const subtitle = document.createElement("div");
                subtitle.className = "address-result__subtitle";
                subtitle.textContent = `지번: ${address.jibunAddressName}`;
                li.appendChild(subtitle);
            }

            const meta = document.createElement("div");
            meta.className = "address-result__meta";
            if (address.buildingName) {
                const badge = document.createElement("span");
                badge.textContent = address.buildingName;
                meta.appendChild(badge);
            }
            if (address.zoneNo) {
                const badge = document.createElement("span");
                badge.textContent = `우편번호 ${address.zoneNo}`;
                meta.appendChild(badge);
            }
            if (address.latitude && address.longitude) {
                const badge = document.createElement("span");
                badge.textContent = `(${address.latitude.toFixed(6)}, ${address.longitude.toFixed(6)})`;
                meta.appendChild(badge);
            }
            if (meta.children.length) {
                li.appendChild(meta);
            }

            const actions = document.createElement("div");
            actions.className = "address-result__actions";

            const useButton = document.createElement("button");
            useButton.type = "button";
            useButton.className = "button button--ghost button--small";
            useButton.dataset.index = String(index);
            useButton.textContent = "이 주소 사용";
            actions.appendChild(useButton);

            li.appendChild(actions);
            els.addressResults.appendChild(li);
        });
    }

    function handleAddressPick(event) {
        const button = event.target.closest("button[data-index]");
        if (!button) return;
        const index = Number(button.dataset.index);
        const address = state.lastAddresses[index];
        if (!address) return;

        state.selectedAddressIndex = index;
        selectAddress(address);
        updateAddressHighlight();
    }

    function selectAddress(address) {
        if (!els.storeLocation) return;

        const chosenAddress = address.roadAddressName || address.addressName || "";
        els.storeLocation.value = chosenAddress;
        els.storeLocation.dataset.latitude = address.latitude ?? "";
        els.storeLocation.dataset.longitude = address.longitude ?? "";

        const extra = [];
        if (address.buildingName) extra.push(address.buildingName);
        if (address.zoneNo) extra.push(`우편번호 ${address.zoneNo}`);
        if (address.latitude && address.longitude) {
            extra.push(`좌표 ${address.latitude.toFixed(5)}, ${address.longitude.toFixed(5)}`);
        }
        updateLocationHelper(extra.join(" · "));
        setFeedback("주소를 입력란에 반영했습니다.", "success");
    }

    function updateAddressHighlight() {
        if (!els.addressResults) return;
        const items = els.addressResults.querySelectorAll(".address-result");
        items.forEach((item) => {
            const idx = Number(item.dataset.index);
            if (Number.isNaN(idx)) {
                item.classList.remove("is-selected");
                return;
            }
            item.classList.toggle("is-selected", idx === state.selectedAddressIndex);
        });
    }

    function updateLocationHelper(message) {
        if (!els.locationHelper) return;
        els.locationHelper.textContent = message && message.length
            ? message
            : "검색 결과에서 “이 주소 사용”을 누르거나 직접 주소를 입력하세요.";
    }

    function updateAddressMeta(payload) {
        if (!els.addressMeta) return;
        const totalCount = payload.totalCount ?? payload.addresses?.length ?? 0;
        const isEnd = payload.isEnd ? "마지막 페이지" : "추가 페이지 존재";
        const pageableCount = payload.pageableCount ?? "-";
        els.addressMeta.textContent = `총 ${totalCount}건 · ${isEnd} · 페이지 수 ${pageableCount}`;
    }

    async function handleStoreSubmit(event) {
        event.preventDefault();
        if (!els.storeName || !els.storeLocation) return;

        const name = els.storeName.value.trim();
        const location = els.storeLocation.value.trim();
        const description = els.storeDescription?.value.trim();

        if (!name || !location) {
            setFeedback("가게 이름과 주소는 필수입니다.", "error");
            return;
        }

        setFeedback("가게 생성 요청 전송 중...", "info");
        els.responseOutput.textContent = "{ }";

        try {
            const response = await fetch("/api/v1/stores", {
                method: "POST",
                headers: {
                    "Authorization": getNormalizedToken(),
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify({
                    name,
                    location,
                    description: description || null
                })
            });

            if (!response.ok) {
                const message = await extractError(response);
                throw new Error(message);
            }

            const payload = await response.json();
            els.responseOutput.textContent = JSON.stringify(payload, null, 2);
            setFeedback("가게가 성공적으로 생성되었습니다.", "success");
        } catch (error) {
            setFeedback(error.message || "가게 생성 요청에 실패했습니다.", "error");
        }
    }

    async function extractError(response) {
        const text = await response.text();
        try {
            const json = JSON.parse(text);
            if (json.message) {
                return `${json.message} (HTTP ${response.status})`;
            }
            return `${JSON.stringify(json)} (HTTP ${response.status})`;
        } catch {
            return text || `요청이 실패했습니다. (HTTP ${response.status})`;
        }
    }

    function setFeedback(message, status) {
        if (!els.feedback) return;
        const baseClass = "feedback";
        els.feedback.className = baseClass;
        if (status === "success") {
            els.feedback.classList.add("feedback--success");
        } else if (status === "error") {
            els.feedback.classList.add("feedback--error");
        }
        els.feedback.textContent = message;
    }
})();
