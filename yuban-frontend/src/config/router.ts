import Index from "../pages/Index.vue";
import Team from "../pages/TeamPage.vue";
import User from "../pages/UserPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import SearchPage from "../pages/SearchPage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import LoginPages from "../pages/LoginPages.vue";

const routes = [
    { path: '/', component: Index },
    { path: '/team', component: Team },
    { path: '/user', component: User },
    { path: '/search', component: SearchPage },
    { path: '/user/edit', component: UserEditPage },
    { path: '/searchResult', component: SearchResultPage },
    { path: '/user/login', component: LoginPages },
]

export default routes;