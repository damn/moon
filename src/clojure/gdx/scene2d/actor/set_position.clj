(ns clojure.gdx.scene2d.actor.set-position
  (:require [clojure.gdx.utils.align :as align])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-position!
  ([^Actor actor [x y]]
   (.setPosition actor x y))
  ([^Actor actor [x y] align]
   (.setPosition actor x y (align/k->value align))))
