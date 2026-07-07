(ns clojure.editor-rebuild
  (:require
            [clojure.remove-actor] [clojure.stage :as stage]
            [clojure.group :as group]
            [clojure.widget-value :refer [map-widget-table-get-value]]
            [clojure.editor-window]))

(defn f!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   :stage/root
                   (group/find-actor "moon.ui.clojure.editor-window"))
        map-widget-table (group/find-actor window "moon.db.schema.map.ui.widget")
        property (map-widget-table-get-value map-widget-table (:db/schemas db))]
    (clojure.remove-actor/f window)
    (stage/add-actor! stage
                 (clojure.editor-window/property-editor-window
                  {:ctx ctx
                   :property property}))))
