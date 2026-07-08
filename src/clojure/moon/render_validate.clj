(ns clojure.moon.render-validate
  (:require [clojure.moon.schema :refer [schema]]
            [clojure.validate-humanize :refer [validate-humanize]]))

(defn f [ctx]
  (validate-humanize schema ctx)
  ctx)
