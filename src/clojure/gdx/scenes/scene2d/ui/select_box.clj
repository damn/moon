(ns clojure.gdx.scenes.scene2d.ui.select-box
  (:require [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.scene2d.ui.select-box :as select-box])
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox
                                               Skin)))

(defmethod actor/create :ui/select-box
  [{:keys [items selected skin]}]
  (doto (SelectBox. ^Skin skin)
    (.setItems ^"[Ljava.lang.Object;" (into-array items))
    (.setSelected selected)))

(extend-type SelectBox
  select-box/SelectBox
  (selected [select-box]
    (.getSelected select-box)))
