(ns moon.entity.body
  (:require [moon.body]
            [moon.entity :as entity]))

(defmethod entity/create :entity/body
  [[_ v] {:keys [ctx/world]}]
  (moon.body/create v world))
