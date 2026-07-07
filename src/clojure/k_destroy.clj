(ns clojure.k-destroy
  (:require [clojure.k-destroy.destroy-audiovisual :as destroy-audiovisual]))

(def k->destroy
  {:entity/destroy-audiovisual destroy-audiovisual/f})
