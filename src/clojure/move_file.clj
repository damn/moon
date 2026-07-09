(ns clojure.move-file
  (:require [clojure.java.nio.file.files :as files]))

(defn f [& args]
  (apply files/move args))
