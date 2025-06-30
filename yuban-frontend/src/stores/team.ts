import { defineStore } from 'pinia'
import type { TeamType } from 'src/models/team.d.ts'  // 确保路径和导出正确

export const useTeamStore = defineStore('team', {
    state: () => ({
        // 直接用 TeamType 而不是再包装一层
        editingTeam: null as TeamType | null,
    }),
    actions: {
        setEditingTeam(team: TeamType) {
            this.editingTeam = team
        },
        clearEditingTeam() {
            this.editingTeam = null
        }
    }
})
