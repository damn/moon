(ns clojure.editor.create-widget-rebuild-editor-window
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.editor.create-widget-property-editor-window :as property-editor-window]
            [clojure.editor.widget-value :refer [map-widget-table-get-value]]
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn rebuild-editor-window!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   :stage/root
                   (group/findActor "moon.ui.clojure.editor-window"))
        map-widget-table (group/findActor window "moon.db.schema.map.ui.widget")
        property (map-widget-table-get-value map-widget-table (:db/schemas db))]
    (actor/remove window)
    (stage/addActor stage
                      (property-editor-window/property-editor-window
                       {:ctx ctx
                        :property property}))))
