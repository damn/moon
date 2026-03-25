(ns moon.scroll-pane-cell
  (:require [moon.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane
                                               Skin
                                               Table)))

(defn create [^Skin skin viewport-height rows]
  (let [^Actor table (doto (-> (Table.)
                               (table/set-opts!
                                {:rows rows
                                 :cell-defaults {:pad 5}}))
                       (.pack)
                       (.setName "scroll-pane-table"))]
    {:actor (doto (ScrollPane. table skin)
              (.setName "moon.ui.widget.scroll-pane-table"))
     :width  (+ (.getWidth table) 50)
     :height (min (- viewport-height 50)
                  (.getHeight table))}))
