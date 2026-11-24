(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.text-button
  "A button with a child [[clojure.com.badlogic.gdx.scenes.scene2d.ui.label]] to display text."
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin TextButton)))

(defn create [^String text ^Skin skin]
  (TextButton. text skin))
