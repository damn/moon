(ns clojure.set-opts
  (:require [clojure.bottom! :as bottom!]
            [clojure.center! :as center!]
            [clojure.colspan! :as colspan!]
            [clojure.expand! :as expand!]
            [clojure.expand-x! :as expand-x!]
            [clojure.expand-y! :as expand-y!]
            [clojure.fill-x! :as fill-x!]
            [clojure.fill-y! :as fill-y!]
            [clojure.height! :as height!]
            [clojure.left! :as left!]
            [clojure.pad! :as pad!]
            [clojure.pad-bottom! :as pad-bottom!]
            [clojure.pad-top! :as pad-top!]
            [clojure.right! :as right!]
            [clojure.width! :as width!]))

(defn f [cell opts]
  (doseq [[option arg] opts]
    (case option
      :fill-x?    (fill-x!/f cell)
      :fill-y?    (fill-y!/f cell)
      :expand?    (expand!/f cell)
      :expand-x?  (expand-x!/f cell)
      :expand-y?  (expand-y!/f cell)
      :bottom?    (bottom!/f cell)
      :colspan    (colspan!/f cell arg)
      :pad        (pad!/f cell arg)
      :pad-top    (pad-top!/f cell arg)
      :pad-bottom (pad-bottom!/f cell arg)
      :width      (width!/f cell arg)
      :height     (height!/f cell arg)
      :center?    (center!/f cell)
      :right?     (right!/f cell)
      :left?      (left!/f cell))))
