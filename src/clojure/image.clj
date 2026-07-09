(ns clojure.image
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image :as image]))

(defn new [& args]
  (apply image/new args))

(defn new-drawable [& args]
  (apply image/new-drawable args))

(defn new-texture [& args]
  (apply image/new-texture args))

(defn set-drawable! [& args]
  (apply image/set-drawable! args))
