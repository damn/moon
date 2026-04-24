(ns clojure.gdx.scene2d.ui.label
  (:require [clojure.scene2d.actor :as actor]
            clojure.scene2d.ui.label)
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defn create
  [{:keys [text skin] :as opts}]
  (doto (Label. ^String text ^Skin skin)
    (actor/set-opts! opts)))

(extend-type Label
  clojure.scene2d.ui.label/Label
  (set-text! [label ^String text]
    (.setText label text)))
