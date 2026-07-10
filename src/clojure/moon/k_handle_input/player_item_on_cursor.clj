(ns clojure.moon.k-handle-input.player-item-on-cursor
  (:require [com.badlogic.gdx.input :as input]
            [gdl.input.buttons :as input-buttons]
            [clojure.mouseover-actor :refer [mouseover-actor]]))

(defn f
  [eid ctx]
  (when (and (input/isButtonJustPressed (:ctx/input ctx) (input-buttons/key-to-value :input.buttons/left))
             (not (mouseover-actor ctx)))
    [[:tx/event eid :drop-item]]))
