(ns editor.rebuild
  (:require [editor.map-widget-table.get-value :as get-value]
            [clojure.scenes.scene2d.actor.remove :refer [remove!]]
            [clojure.scenes.scene2d.group.find-actor :refer [find-actor]]
            [editor.window]
            [clojure.scenes.scene2d.stage.add-actor :refer [add-actor!]]))

(defn f!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   :stage/root
                   (find-actor "moon.ui.editor.window"))
        map-widget-table (find-actor window "moon.db.schema.map.ui.widget")
        property (get-value/f map-widget-table (:db/schemas db))]
    (remove! window)
    (add-actor! stage
                (editor.window/property-editor-window
                 {:ctx ctx
                  :property property}))))
