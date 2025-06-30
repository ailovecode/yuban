import Index from "../pages/Index.vue";
import Team from "../pages/TeamPage.vue";
import User from "../pages/UserPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import LoginPages from "../pages/LoginPages.vue";
import TeamAddPage from "../pages/TeamAddPage.vue";
import SearchPage from "../pages/SearchPage.vue";
import UserTeamPage from "../pages/UserTeamPage.vue";
import EditTeamPage from "../pages/EditTeamPage.vue";

const routes = [
    { path: '/',               name: 'Home',          component: Index },
    { path: '/team',           name: 'TeamPage',      component: Team },
    { path: '/team/add',       name: 'TeamAdd',       component: TeamAddPage },

    // 我 的 队伍 父页面（可选，如果你还需要在这里放置 tabs 组件）
    { path: '/team/myself',    name: 'UserTeam',      component: UserTeamPage },

    { path: '/team/edit',    name: 'EditTeam',      component: EditTeamPage  },

    { path: '/user',           name: 'UserPage',      component: User },
    { path: '/user/edit',      name: 'UserEdit',      component: UserEditPage },

    { path: '/search',         name: 'SearchPage',    component: SearchPage },
    { path: '/searchResult',   name: 'SearchResult',  component: SearchResultPage },

    { path: '/user/login',     name: 'Login',         component: LoginPages },
];
export default routes;