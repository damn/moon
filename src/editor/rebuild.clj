(ns editor.rebuild
  (:require
            [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.stage.add-actor :as add-actor]
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
    (add-actor/f stage
                 (editor.window/property-editor-window
                  {:ctx ctx
                   :property property}))))
