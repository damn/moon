(ns editor.rebuild
  (:require [editor.map-widget-table.get-value :as get-value]
            [editor.window]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.group :as group]
            [gdx.stage :as stage]))

(defn f!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   (stage/find-actor "moon.ui.editor.window"))
        map-widget-table (group/find-actor window "moon.db.schema.map.ui.widget")
        property (get-value/f map-widget-table (:db/schemas db))]
    (actor/remove! window)
    (stage/add-actor! stage
                      (editor.window/property-editor-window
                       {:ctx ctx
                        :property property}))))
