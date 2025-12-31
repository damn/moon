(ns moon.application.open-editor
  (:require [gdl.ui.stage :as stage]
            [moon.db :as db]))

; is this not same like a :tx/ ?
(defn do!
  [{:keys [ctx/db
           ctx/graphics
           ctx/skin
           ctx/stage]}
   property-type]
  (stage/add-actor! stage
                    {:type :actor/editor-overview-window
                     :db db
                     :graphics graphics
                     :skin skin
                     :property-type property-type
                     :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                      ; why not a 'ctx' function
                                      ; or transaction
                                      ; why do I need {:type} which is then a fn ?
                                      (stage/add-actor! stage
                                                        {:type :actor/editor-window
                                                         :ctx ctx
                                                         :property (db/get-raw db id)}))}))
