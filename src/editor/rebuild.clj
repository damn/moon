(ns editor.rebuild
  (:require [editor.map-widget-table.get-value :as get-value]
            [clojure.gdx.scene2d.group.find-actor :refer [find-actor]]
            [editor.window]
            [gdx.stage :as stage]))

(defn f!
  [{:keys [ctx/db
           ctx/stage]
    :as ctx}]
  (let [window (-> stage
                   (stage/find-actor "moon.ui.editor.window"))
        map-widget-table (find-actor window "moon.db.schema.map.ui.widget")
        property (get-value/f map-widget-table (:db/schemas db))]
    (.remove window)
    (stage/add-actor! stage
                      (editor.window/property-editor-window
                       {:ctx ctx
                        :property property}))))
