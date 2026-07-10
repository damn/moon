(ns clojure.stage
  (:require [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn act! [& args]
  (apply stage/act args))

(defn add-actor! [& args]
  (apply stage/addActor args))

(defn draw! [& args]
  (apply stage/draw args))

(defn hit [& args]
  (apply stage/hit args))
