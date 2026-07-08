(ns clojure.render-validate
  (:require [clojure.moon.schema :refer [schema]]
            [clojure.validate-humanize :refer [validate-humanize]]))

(defn step [ctx]
  (validate-humanize schema ctx)
  ctx)
