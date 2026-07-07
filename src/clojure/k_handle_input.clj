(ns clojure.k-handle-input
  (:require [clojure.handle-input-player-idle :as handle-input-player-idle]
            [clojure.handle-input-player-item-on-cursor :as handle-input-player-item-on-cursor]
            [clojure.handle-input-player-moving :as handle-input-player-moving]))

(def k->handle-input
  {:player-idle handle-input-player-idle/f
   :player-moving handle-input-player-moving/f
   :player-item-on-cursor handle-input-player-item-on-cursor/f})
