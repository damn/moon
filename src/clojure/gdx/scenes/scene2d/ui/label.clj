(ns clojure.gdx.scenes.scene2d.ui.label
  (:require [com.badlogic.gdx.scenes.scene2d.ui.label :as label]))

(defn create [text skin]
  (label/new text skin))
