(ns clojure.rename-dev
  (:require [clojure.move-and-rename :as move-and-rename]
            [clojure.rename :as rename]))

(comment
  ;; Move file + update all references under src/, resources/, test/
  (move-and-rename/f "clojure.shape-drawer"
                     "clojure.shape-drawer")

  ;; Text-only rename (no file move):
  (rename/f "clojure.utils-click-listener" "clojure.scene2d.utils.click-listener")
  )
