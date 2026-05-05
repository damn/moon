(ns clojure.gdx.scene2d.stage
  (:require [com.badlogic.gdx.scenes.scene2d.ctx-stage :as stage]
            [clojure.gdx.scene2d.group :as group]
            [clojure.gdx.utils.viewport :as viewport]))

(def create stage/create)
(def ctx stage/ctx)
(def set-ctx! stage/set-ctx!)
(def add-actor! stage/add-actor!)
(def viewport stage/viewport)
(def act! stage/act!)
(def draw! stage/draw!)

(defn find-actor [stage name]
  (-> stage
      stage/root
      (group/find-actor name)))

(defn mouseover-actor [stage position]
  (stage/hit stage
             (viewport/unproject (stage/viewport stage) position)
             true))

