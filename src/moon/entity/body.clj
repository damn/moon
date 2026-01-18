(ns moon.entity.body
  (:require [moon.body]))

(defn create
  [[_ v] ctx]
  (moon.body/create v ctx))
