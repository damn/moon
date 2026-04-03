(ns clj.api.com.badlogic.gdx.math.rectangle
  (:refer-clojure :exclude [contains?])
  (:import (com.badlogic.gdx.math Rectangle)))

(defn create [x y width height]
  (Rectangle. x y width height))

(defn overlaps? [^Rectangle rectangle ^Rectangle other-rectangle]
  ; return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
  (.overlaps rectangle other-rectangle))

(defn contains? [^Rectangle rectangle [x y]]
  ; return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
  (.contains rectangle x y))
