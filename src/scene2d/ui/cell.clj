(ns scene2d.ui.cell
  (:require [clojure.gdx.cell.bottom! :as bottom!]
            [clojure.gdx.cell.center! :as center!]
            [clojure.gdx.cell.colspan! :as colspan!]
            [clojure.gdx.cell.expand! :as expand!]
            [clojure.gdx.cell.expand-x! :as expand-x!]
            [clojure.gdx.cell.expand-y! :as expand-y!]
            [clojure.gdx.cell.fill-x! :as fill-x!]
            [clojure.gdx.cell.fill-y! :as fill-y!]
            [clojure.gdx.cell.height! :as height!]
            [clojure.gdx.cell.left! :as left!]
            [clojure.gdx.cell.pad! :as pad!]
            [clojure.gdx.cell.pad-bottom! :as pad-bottom!]
            [clojure.gdx.cell.pad-top! :as pad-top!]
            [clojure.gdx.cell.right! :as right!]
            [clojure.gdx.cell.width! :as width!]))

(defn set-opts! [cell opts]
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
