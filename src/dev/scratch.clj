(comment

 (require '[moon.db.build :refer [build]]
          '[clojure.gdx.gdx.app :as app]
          '[clojure.gdx.application.post-runnable! :as post-runnable!])
 (post-runnable!/f (app/f)
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
