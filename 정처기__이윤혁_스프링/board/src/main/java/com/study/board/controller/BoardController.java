package com.study.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/list")
    public String listBoards(@RequestParam(name = "searchKeyword", defaultValue = "") String searchKeyword,
                             @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC) Pageable pageable,
                             Model model) {
        Page<Board> boardPage;

        if (searchKeyword.isEmpty()) {
            boardPage = boardService.getAllBoards(pageable);
        } else {
            boardPage = boardService.findByTitleContaining(searchKeyword, pageable);
        }

        model.addAttribute("boards", boardPage);
        model.addAttribute("searchKeyword", searchKeyword);

        return "board/list";
    }

    @GetMapping("/new")
    public String newBoardForm(Model model) {
        model.addAttribute("board", new Board());
        return "board/form";
    }

    @PostMapping
    public String saveBoard(@ModelAttribute Board board) {
        boardService.saveBoard(board);
        return "redirect:/board/list";
    }

    @GetMapping("/{id}")
    public String viewBoard(@PathVariable("id") Long id, Model model) {
        model.addAttribute("board", boardService.getBoardById(id));
        return "board/view";
    }

    @GetMapping("/{id}/edit")
    public String editBoardForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("board", boardService.getBoardById(id));
        return "board/form";
    }

    @PostMapping("/{id}")
    public String updateBoard(@PathVariable("id") Long id, @ModelAttribute Board board, Model model, RedirectAttributes redirectAttributes) {
        try {
            board.setId(id);
            boardService.saveBoard(board);
            redirectAttributes.addFlashAttribute("message", "Board updated successfully");
            return "redirect:/board/" + id;
        } catch (Exception e) {
            model.addAttribute("error", "Board could not be updated: " + e.getMessage());
            return "board/form";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            boardService.deleteBoard(id);
            redirectAttributes.addFlashAttribute("message", "Board deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Board could not be deleted: " + e.getMessage());
        }
        return "redirect:/board/list";
    }
}
