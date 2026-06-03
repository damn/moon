(ns moon.schemas.build-values
  (:require [moon.schemas.create-value :refer [create-value]]))

(defn build-values [schemas property db]
  (reduce (fn [m k]
            (assoc m k
                   (try (create-value (get schemas k) (k m) db)
                        (catch Throwable t
                          (throw (ex-info " " {:k k
                                               :v (k m)} t))))))
          property
          (keys property)))
