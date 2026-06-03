(ns gdx.scenes.scene2d.ui.label
  (:require [clojure.gdx.scene2d.ui.label :as label]))

(defn create
  [opts]
  (label/create opts))

(defn set-text! [label text]
  (label/set-text! label text))
