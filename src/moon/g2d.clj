(ns moon.g2d
  (:require [moon.position :as position]))

(defprotocol Positions
  (posis [_]))

(defn adjacent-wall-positions [g2d]
  (filter (fn [position]
            (and (= :wall (get g2d position))
                 (some #(= :ground (get g2d %))
                       (position/get-8-neighbours position))))
          (posis g2d)))
