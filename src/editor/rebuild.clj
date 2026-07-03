(ns editor.rebuild
  (:require [clojure.gdx.actor.remove :as remove]
            [clojure.gdx.stage.add-actor :as add-actor]
            [editor.map-widget-table.get-value :as get-value]
            [editor.window])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn f!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   :stage/root
                   (#(Group/.findActor % "moon.ui.editor.window")))
        map-widget-table (Group/.findActor window "moon.db.schema.map.ui.widget")
        property (get-value/f map-widget-table (:db/schemas db))]
    (remove/f window)
    (add-actor/f stage
                 (editor.window/property-editor-window
                  {:ctx ctx
                   :property property}))))
