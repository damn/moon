(ns gdx.stage
  (:require [clojure.gdx.scene2d.stage :as stage]
            [gdx.scenes.scene2d.group :as group]
            [gdx.viewport :as viewport]))

(defn create [viewport batch]
  (stage/create viewport batch))

(defn set-ctx! [stage ctx]
  (stage/set-ctx! stage ctx))

(defn add-actor! [stage actor]
  (stage/add-actor! stage actor))

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
