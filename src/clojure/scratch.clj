(comment

 (require '[clojure.db.build :refer [build]]
          '[com.badlogic.gdx.application :as application]
          '[com.badlogic.gdx.gdx :as gdx]
          )
 (application/postRunnable (gdx/app)
  (fn []
    (let [{:keys [ctx/db]
           :as ctx} @state]
      (txs/handle! ctx
                   [[:tx/spawn-creature
                     {:position [35 73]
                      :creature-property (build db :creatures/dragon-red)
                      :components {:entity/fsm {:fsm :fsms/npc
                                                :initial-state :npc-sleeping}
                                   :entity/faction :evil}}]]))))
 )
