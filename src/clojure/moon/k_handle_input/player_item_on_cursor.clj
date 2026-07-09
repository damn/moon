(ns clojure.moon.k-handle-input.player-item-on-cursor
  (:require [clojure.input-button-just-pressed :as button-just-pressed?]
            [clojure.mouseover-actor :refer [mouseover-actor]]))

(defn f
  [eid ctx]
  (when (and (button-just-pressed?/f (:ctx/input ctx) :input.buttons/left)
             (not (mouseover-actor ctx)))
    [[:tx/event eid :drop-item]]))
