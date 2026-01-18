(ns moon.ui.scroll-pane-cell
  (:require [moon.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane
                                               Skin)))

(defn create [skin viewport-height rows]
  (let [table (ui/actor
               {:type :ui/table
                :rows rows
                :actor/name "scroll-pane-table"
                :cell-defaults {:pad 5}
                :pack? true})]
    {:actor (doto (ScrollPane. table skin)
              (.setName "moon.ui.widget.scroll-pane-table"))
     :width  (+ (.getWidth table) 50)
     :height (min (- viewport-height 50)
                  (.getHeight table))}))
