(ns moon.game.open-editor
  (:require [moon.db :as db]
            [moon.ui.stage :as stage]))

; is this not same like a :tx/ ?
(defn do!
  [{:keys [ctx/db
           ctx/graphics
           ctx/stage]}
   property-type]
  (stage/add-actor! stage
                    {:type :actor/editor-overview-window
                     :db db
                     :graphics graphics
                     :property-type property-type
                     :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                      ; why not a 'ctx' function
                                      ; or transaction
                                      ; why do I need {:type} which is then a fn ?
                                      (stage/add-actor! stage
                                                        {:type :actor/editor-window
                                                         :ctx ctx
                                                         :property (db/get-raw db id)}))}))
