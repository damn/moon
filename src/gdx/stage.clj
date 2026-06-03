(ns gdx.stage
  (:require [clojure.gdx.scene2d.stage :as stage]
            [clojure.gdx.scene2d.group.find-actor :as group]
            [clojure.gdx.utils.viewport :as viewport]))

(defn act! [stage]
  (stage/act! stage))

(defn draw! [stage]
  (stage/draw! stage))

(defn find-actor [stage name]
  (-> stage
      stage/root
      (group/find-actor name)))

(defn mouseover-actor [stage position]
  (stage/hit stage
             (-> stage
                 :stage/viewport
                 (viewport/unproject position))))
