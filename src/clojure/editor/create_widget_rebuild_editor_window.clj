(ns clojure.editor.create-widget-rebuild-editor-window
  (:require [clojure.actor.remove-actor]
            [clojure.editor.create-widget-property-editor-window :as property-editor-window]
            [clojure.editor.widget-value :refer [map-widget-table-get-value]]
            [clojure.group :as group]
            [clojure.stage :as stage]))

(defn rebuild-editor-window!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   :stage/root
                   (group/find-actor "moon.ui.clojure.editor-window"))
        map-widget-table (group/find-actor window "moon.db.schema.map.ui.widget")
        property (map-widget-table-get-value map-widget-table (:db/schemas db))]
    (clojure.actor.remove-actor/f window)
    (stage/add-actor! stage
                      (property-editor-window/property-editor-window
                       {:ctx ctx
                        :property property}))))
