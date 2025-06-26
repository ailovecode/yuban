import myAxios from "../plugins/myAxios"

export const getCurrentUser = () => {
    return myAxios.get("/user/currentUser");
}

export const logoutCurrentUser = () => {
    return myAxios.post("/user/logout");
}