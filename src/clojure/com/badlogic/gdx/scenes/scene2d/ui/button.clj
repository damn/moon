(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.button
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button)))

(defn button? [x]
  (instance? Button x))
