(ns clojure.editor.rebuild
  (:require [clojure.editor.map-widget-table.get-value :as get-value]
            [gdl.remove :refer [remove!]]
            [gdl.group.find-actor :refer [find-actor]]
            [clojure.editor.window]
            [gdl.stage.add-actor :refer [add-actor!]]))

(defn f!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   :stage/root
                   (find-actor "moon.ui.clojure.editor.window"))
        map-widget-table (find-actor window "moon.db.schema.map.ui.widget")
        property (get-value/f map-widget-table (:db/schemas db))]
    (remove! window)
    (add-actor! stage
                (clojure.editor.window/property-editor-window
                 {:ctx ctx
                  :property property}))))
