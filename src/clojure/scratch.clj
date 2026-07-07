(comment

 (require '[clojure.build :refer [build]]
          '[clojure.gdx :as gdx]
          '[clojure.post-runnable :as post-runnable])
 (post-runnable/f (gdx/app)
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
