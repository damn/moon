(ns clojure.gdx.scene2d.ui.select-box
  (:require clojure.scene2d.ui.select-box)
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox
                                               Skin)))

(defn create [{:keys [items selected skin]}]
  (doto (SelectBox. ^Skin skin)
    (.setItems ^"[Ljava.lang.Object;" (into-array items))
    (.setSelected selected)))

(extend-type SelectBox
  clojure.scene2d.ui.select-box/SelectBox
  (selected [select-box]
    (.getSelected select-box)))
