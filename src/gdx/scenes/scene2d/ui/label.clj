(ns gdx.scenes.scene2d.ui.label
  (:refer-clojure :exclude [class])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.label :as label]))

(def class label/class)

(defn create [text skin]
  (label/new text skin))

(defn set-text! [label text]
  (label/setText label text))
