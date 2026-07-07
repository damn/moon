(ns editor.rebuild
  (:require [clojure.stage :as stage]
            [clojure.group :as group]
            [clojure.actor :as actor]
            [editor.map-widget-table.get-value :as get-value]
            [editor.window]))

(defn f!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   :stage/root
                   (group/find-actor "moon.ui.editor.window"))
        map-widget-table (group/find-actor window "moon.db.schema.map.ui.widget")
        property (get-value/f map-widget-table (:db/schemas db))]
    (actor/remove! window)
    (stage/add-actor! stage
                 (editor.window/property-editor-window
                  {:ctx ctx
                   :property property}))))
