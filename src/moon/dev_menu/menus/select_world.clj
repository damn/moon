(ns moon.dev-menu.menus.select-world)

(defn create [_ctx]
  {:label "Select World"
   :items (for [world-fn ["world_fns/vampire.edn"
                          "world_fns/uf_caves.edn"
                          "world_fns/modules.edn"]]
            {:label (str "Start " world-fn)
             :on-click (fn [actor {:keys [ctx/stage] :as ctx}]
                         #_(let [rebuild-actors! nil
                                 #_(fn rebuild-actors! [stage ctx]
                                     (.clear stage)
                                     ((requiring-resolve 'moon.application.create.add-actors/step) ctx))
                                 create-world nil
                                 #_(requiring-resolve 'moon.application.create.world/step)
                                 ui stage
                                 stage (Actor/.getStage actor)]  ; get before clear, otherwise the actor does not have a stage anymore
                             (rebuild-actors! ui ctx)
                             #_(Disposable/.dispose (:ctx/tiled-map ctx))
                             (set! (.ctx ^Stage stage) (create-world ctx world-fn))))})})
