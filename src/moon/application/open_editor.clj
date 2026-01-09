(ns moon.application.open-editor
  (:require [gdl.ui.stage :as stage]
            [moon.db :as db]
            [moon.ui.editor.overview-window :as overview-window]))

(defn do!
  [{:keys [ctx/db
           ctx/graphics
           ctx/skin
           ctx/stage]}
   property-type]
  (stage/add-actor! stage
                    (overview-window/create
                     {:db db
                      :graphics graphics
                      :skin skin
                      :property-type property-type
                      :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                       (stage/add-actor! stage
                                                         (stage/build
                                                          {:type :actor/editor-window
                                                           :ctx ctx
                                                           :property (db/get-raw db id)})))})))
