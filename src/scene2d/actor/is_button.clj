(ns scene2d.actor.is-button
  (:require [scene2d.actor.get-parent :refer [get-parent]])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button)))

(let [button-class? (fn [actor]
                      (some #(= Button %) (supers (class actor))))]
  (defn f [actor]
    (or (button-class? actor)
        (and (get-parent actor)
             (button-class? (get-parent actor))))))
