(ns clojure.moon.k-handle-input
  (:require [clojure.moon.k-handle-input.player-idle :as player-idle]
            [clojure.moon.k-handle-input.player-item-on-cursor :as player-item-on-cursor]
            [clojure.moon.k-handle-input.player-moving :as player-moving]))

(def k->handle-input
  {:player-idle player-idle/f
   :player-moving player-moving/f
   :player-item-on-cursor player-item-on-cursor/f})
