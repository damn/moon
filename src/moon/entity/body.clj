(ns moon.entity.body
  (:require [moon.body]
            [moon.entity :as entity]))

(defmethod entity/create :entity/body
  [[_ v] ctx]
  (moon.body/create v ctx))
